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
package org.cfr.capsicum.spring.support;

import org.apache.cayenne.exp.ExpressionException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Exception wrapping Cayenne ExpressionException.
 */
public class CayenneExpressionException extends InvalidDataAccessApiUsageException {

    /**
     * 
     */
    private static final long serialVersionUID = -1747876574019592478L;

    protected String expressionString;

    public CayenneExpressionException(ExpressionException ex) {
        super(ex.getUnlabeledMessage(), ex);
        this.expressionString = ex.getExpressionString();
    }

    public String getExpressionString() {
        return expressionString;
    }
}