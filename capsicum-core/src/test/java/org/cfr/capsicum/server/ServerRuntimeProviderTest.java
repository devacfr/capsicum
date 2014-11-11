/**
 * Copyright 2014 devacfr<christophefriederich@mac.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfr.capsicum.server;

import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.tutorial.persistent.Artist;
import org.apache.cayenne.tutorial.persistent.Gallery;
import org.apache.cayenne.tutorial.persistent.Painting;
import org.cfr.capsicum.access.DataDomainDefinition;
import org.cfr.commons.testing.EasyMockTestCase;
import org.joda.time.DateMidnight;
import org.junit.Test;

/**
 * @author devacfr<christophefriederich@mac.com>
 *
 */
public class ServerRuntimeProviderTest extends EasyMockTestCase {

    @Test
    public void simpleTest() throws Exception {
        ServerRuntimeProvider cayenneRuntimeContext = new ServerRuntimeProvider();
        cayenneRuntimeContext.setDataDomainDefinitions(Arrays.asList(DataDomainDefinition.builder()
            .name("project")
            .location("classpath:cayenne-project.xml")
            .build()));
        cayenneRuntimeContext.afterPropertiesSet();
        try {
            CayenneRuntime cayenneRuntime = cayenneRuntimeContext.get();
            ObjectContext context = cayenneRuntime.newContext();
            saveData(context);

            SelectQuery<Painting> select1 = new SelectQuery<Painting>(Painting.class);
            List<Painting> paintings1 = context.select(select1);
            assertEquals(2, paintings1.size());

            Expression qualifier2 = ExpressionFactory.likeIgnoreCaseExp(Painting.NAME.getName(), "gi%");
            SelectQuery<Painting> select2 = new SelectQuery<Painting>(Painting.class, qualifier2);
            List<Painting> paintings2 = context.select(select2);
            assertEquals(1, paintings2.size());

        } finally {
            cayenneRuntimeContext.destroy();
        }
    }

    private void saveData(final ObjectContext context) {
        Artist picasso = context.newObject(Artist.class);
        picasso.setName("Pablo Picasso");
        picasso.setDateOfBirth(new DateMidnight(1881, 10, 25).toDate());

        Gallery metropolitan = context.newObject(Gallery.class);
        metropolitan.setName("Metropolitan Museum of Art");

        Painting girl = context.newObject(Painting.class);
        girl.setName("Girl Reading at a Table");

        Painting stein = context.newObject(Painting.class);
        stein.setName("Gertrude Stein");

        picasso.addToPaintings(girl);
        picasso.addToPaintings(stein);

        girl.setGallery(metropolitan);
        stein.setGallery(metropolitan);

        context.commitChanges();

    }
}
