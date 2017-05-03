/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.simbasecurity.core.saml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

class XMLErrorHandler extends DefaultHandler {

    private static final Logger log = LoggerFactory.getLogger(XMLErrorHandler.class);
    private static final Marker FATAL = MarkerFactory.getMarker("FATAL");
    private List<String> errorXML = new ArrayList<>();

    @Override
    public void error(SAXParseException e) throws SAXException {
        errorXML.add("ERROR: " + (e.getMessage()));
        log.error("ERROR: " + (e.getMessage()));
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        errorXML.add("FATALERROR: " + (e.getMessage()));
        log.error(FATAL, "FATALERROR: " + (e.getMessage()));
    }

    List<String> getErrorXML() {
        return errorXML;
    }

    public void setErrorXML(List<String> errorXML) {
        this.errorXML = errorXML;
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        errorXML.add("WARNING: " + (e.getMessage()));
        log.warn("WARNING: " + (e.getMessage()));
    }
}
