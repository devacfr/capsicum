package org.cfr.capsicum.user;

import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.CayenneException;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.QueryChain;
import org.apache.cayenne.query.SQLTemplate;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.capsicum.core.CayenneTemplate;
import org.cfr.capsicum.core.ICayenneCallback;
import org.cfr.capsicum.domain.user.Address;
import org.cfr.capsicum.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserStore extends CayenneTemplate implements IUserStore {

    private static UserStore store;

    @Autowired
    public UserStore(ICayenneRuntimeContext cayenneRuntimeFactory) {
        super(cayenneRuntimeFactory);
        store = this;
    }

    @Override
    public List<User> findAllUser() {
        return this.find(User.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertTestUsersInTransaction(boolean rollback) {
        Creator.create("Neal", "Adams");
        Creator.create("Jim", "Steranko");
        Creator.create("Bruce", "Wayne");
        this.commitChanges();
        if (rollback) {
            throw new RuntimeException("expection");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void insertTestUsersInRequiresNewTransaction(boolean rollback) {
        Creator.create("Will", "Smith");
    }

    @Override
    public void deleteAll() {
        this.execute(new ICayenneCallback() {

            @Override
            public Object doInCayenne(ObjectContext context) throws CayenneException, CayenneRuntimeException {
                QueryChain query = new QueryChain();
                query.addQuery(new SQLTemplate(User.class, "DELETE FROM USER_TABLE"));
                context.performGenericQuery(query);
                return null;
            }
        });

    }

    public abstract static class Builder {

        protected String firstname;

        protected String lastname;

        protected List<Address> addresses;

        public Builder firstName(String lastname) {
            this.firstname = lastname;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastname = lastName;
            return this;
        }

        public Builder address(Address address) {
            if (addresses == null)
                this.addresses = Lists.newArrayList();
            this.addresses.add(address);
            return this;
        }

        public Builder addresses(Iterable<Address> addresses) {
            this.addresses = Lists.newArrayList(addresses);
            return this;
        }

        public Builder addresses(Iterator<Address> addresses) {
            this.addresses = Lists.newArrayList(addresses);
            return this;
        }

        public Builder addresses(Address[] addresses) {
            this.addresses = Lists.newArrayList(addresses);
            return this;
        }

        protected abstract User newInstance();

        public User build() {
            User item = newInstance();
            item.setFirstName(firstname);
            item.setLastName(lastname);
            if (this.addresses != null) {
                for (Address addr : this.addresses) {
                    item.addToAddresses(addr);
                }
            }

            return item;
        }
    }

    public static class Creator extends Builder {

        public static User create(String firstName, String lastName, Address... addresses) {
            return ((Creator) new Creator().firstName(firstName).lastName(lastName).addresses(addresses)).build();
        }

        @Override
        protected User newInstance() {
            return store.createInstance(User.class);
        }

    }

}
