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
package org.cfr.capsicum.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.easymock.IExpectationSetters;
import org.easymock.IMockBuilder;
import org.easymock.internal.LastControl;
import org.easymock.internal.MocksControl;
import org.easymock.internal.matchers.Captures;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * This class allow to migrate form JUnit 3.x syntax to JUnit 4.</p>
 * it is also Mock facility.
 * @author cfriedri
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public abstract class EasyMockTestCase extends Assert {

    /** Tracks all EasyMock objects created for a test. */
    private final List<Object> mocks = new ArrayList<Object>();

    private final boolean resetBefore;

    public EasyMockTestCase() {
        this.resetBefore = true;
    }

    public EasyMockTestCase(boolean resetBefore) {
        this.resetBefore = resetBefore;
    }

    public final String getPackageName() {
        return this.getClass().getPackage().getName().replace('.', '/');
    }

    @Before
    public void setUp() throws Exception {
        if (this.resetBefore) {
            reset();
            mocks.clear();
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Switches order checking of the given mock object (more exactly: the
     * control of the mock object) the on and off. For details, see the EasyMock
     * documentation.
     *
     * @param mock
     *            the mock object.
     * @param state
     *            <code>true</code> switches order checking on,
     *            <code>false</code> switches it off.
     */
    public static void checkOrder(final Object mock, final boolean state) {
        getControl(mock).checkOrder(state);
    }

    private static MocksControl getControl(final Object mock) {
        return MocksControl.getControl(mock);
    }

    /**
     * Reports an argument matcher. This method is needed to define own argument
     * matchers. For details, see the EasyMock documentation.
     *
     * @param matcher
     */
    public static void reportMatcher(final IArgumentMatcher matcher) {
        LastControl.reportMatcher(matcher);
    }

    /**
     *
     * @return
     */
    public static Object[] getCurrentArguments() {
        return EasyMock.getCurrentArguments();
    }

    /**
     *
     * @param value
     * @return
     */
    protected <T> IExpectationSetters<T> expect(T value) {
        return EasyMock.expect(value);
    }

    /**
     *
     * @return
     */
    protected <T> IExpectationSetters<T> expectLastCall() {
        return EasyMock.expectLastCall();
    }

    /**
     * Expects any Object argument. For details, see the EasyMock documentation.
     * This matcher (and {@link #anyObject(Class)}) can be used in these three
     * ways:
     * <ul>
     * <li><code>(T)EasyMock.anyObject() // explicit cast</code></li>
     * <li>
     * <code>EasyMock.&lt;T&gt; anyObject() // fixing the returned generic</code>
     * </li>
     * <li>
     * <code>EasyMock.anyObject(T.class) // pass the returned type in parameter</code>
     * </li>
     * </ul>
     *
     * @param <T>
     *            type of the method argument to match
     * @return <code>null</code>.
     */
    public <T> T anyObject() {
        return EasyMock.<T> anyObject();
    }

    /**
     * Expects any Object argument. For details, see the EasyMock documentation.
     * To work well with generics, this matcher can be used in three different
     * ways. See {@link #anyObject()}.
     *
     * @param <T>
     *            type of the method argument to match
     * @param clazz
     *            the class of the argument to match
     * @return <code>null</code>.
     */
    public <T> T anyObject(Class<T> cl) {
        return EasyMock.anyObject(cl);
    }

    /**
     * Creates a strict mock object for the given class, adds it to the internal
     * list of all mocks, and returns it.
     *
     * @param clazz Class to be mocked.
     * @return A mock instance of the given type.
     **/
    protected <T> T mock(Class<T> clazz) {
        return mock(clazz, false);
    }

    /**
     * Creates a strict or nice mock object for the given class, adds it to the internal
     * list of all mocks, and returns it.
     *
     * @param clazz Class to be mocked.
     * @param strict whether or not to make a strict mock
     * @return A mock instance of the given type.
     **/
    protected <T> T mock(Class<T> clazz, boolean strict) {
        T m = strict ? EasyMock.createMock(clazz) : EasyMock.createNiceMock(clazz);
        mocks.add(m);
        return m;
    }

    /**
     * Creates a nice mock object for the given class, adds it to the internal
     * list of all mocks, and returns it.
     *
     * @param clazz Class to be mocked.
     * @return A mock instance of the given type.
     **/
    protected <T> T mock(Class<T> clazz, Method... methods) {
        return mock(clazz, methods, false);
    }

    /**
     * Creates a strict mock object for the given class, adds it to the internal
     * list of all mocks, and returns it.
     *
     * @param clazz Class to be mocked.
     * @return A mock instance of the given type.
     **/
    protected <T> T mock(Class<T> clazz, Method[] methods, boolean strict) {
        IMockBuilder<T> builder = EasyMock.createMockBuilder(clazz).addMockedMethods(methods);
        T m = strict ? builder.createMock() : builder.createNiceMock();
        mocks.add(m);

        return m;
    }

    /**
    /**
     * Sets each mock to replay mode in the order they were created. Call this after setting
     * all of the mock expectations for a test.
     */
    protected void replay() {
        EasyMock.replay(mocks.toArray());
    }

    protected void replay(Object mock) {
        EasyMock.replay(mock);
    }

    /**
     * Verifies each mock in the order they were created. Call this at the end of each test
     * to verify the expectations were satisfied.
     */
    protected void verify() {
        EasyMock.verify(mocks.toArray());
    }

    /**
     * Verifies the given mock objects (more exactly: the controls of the mock objects).
     * @param mock the mock objects.
     */
    protected void verify(Object mock) {
        EasyMock.verify(mock);
    }

    /**
     * Resets all of the mocks.
     */
    protected void reset() {
        EasyMock.reset(mocks.toArray());
    }

    /**
     * Resets the given mock objects (more exactly: the controls of the mock objects).
     * For details, see the EasyMock documentation.
     * @param mock the mock objects.
     */
    protected void reset(Object mock) {
        EasyMock.reset(mock);
    }

    /**
     * Expect any object but captures it for later use.
     *
     * @param <T>
     *            Type of the captured object
     * @param captured
     *            Where the parameter is captured
     * @return <code>null</code>
     */
    public static <T> T capture(final Capture<T> captured) {
        reportMatcher(new Captures<T>(captured));
        return null;
    }

    /**
     * Expects an Object that is equal to the given value.
     *
     * @param <T>
     *            type of the method argument to match
     * @param value
     *            the given value.
     * @return <code>null</code>.
     */
    public <T> T eq(final T value) {
        return EasyMock.eq(value);
    }
}
