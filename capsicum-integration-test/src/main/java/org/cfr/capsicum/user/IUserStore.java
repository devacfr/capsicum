package org.cfr.capsicum.user;

import java.util.List;

import org.cfr.capsicum.domain.user.User;

public interface IUserStore {

    public List<User> findAllUser();

    public void insertTestUsersInTransaction(boolean rollback);

    public void deleteAll();

    void insertTestUsersInRequiresNewTransaction(boolean rollback);

}