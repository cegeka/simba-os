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
package org.simbasecurity.common.xpath;

import javax.xml.namespace.NamespaceContext;
import java.util.*;

import static javax.xml.XMLConstants.*;
import static org.simbasecurity.common.xpath.WSSEConstants.*;

public final class UserNameTokenNamespaceContext implements NamespaceContext {

    private static final Map<String, Collection<String>> NAMESPACE_URI_TO_PREFIX_MAP = createNameSpaceURIToPrefixMap();
    private static final Map<String, String> PREFIX_TO_NAMESPACE_URI_MAP = createPrefixToNameSpaceURIMap();

    private static Map<String, Collection<String>> createNameSpaceURIToPrefixMap() {
        final Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();

        map.put(WSSE_NAMESPACE_URI, Collections.singleton(WSSE_PREFIX));
        map.put(WSU_NAMESPACE_URI, Collections.singleton(WSU_PREFIX));
        map.put(SOAP_ENVELOPE_URI, Collections.singleton(SOAP_ENVELOPE_PREFIX));
        map.put(NULL_NS_URI, Collections.singleton(DEFAULT_NS_PREFIX));
        map.put(XML_NS_URI, Collections.singleton(XML_NS_PREFIX));
        map.put(XMLNS_ATTRIBUTE_NS_URI, Collections.singleton(XMLNS_ATTRIBUTE));

        return map;
    }

    private static Map<String, String> createPrefixToNameSpaceURIMap() {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(WSSE_PREFIX, WSSE_NAMESPACE_URI);
        map.put(WSU_PREFIX, WSU_NAMESPACE_URI);
        map.put(SOAP_ENVELOPE_PREFIX, SOAP_ENVELOPE_URI);
        map.put(DEFAULT_NS_PREFIX, NULL_NS_URI);
        map.put(XML_NS_PREFIX, XML_NS_URI);
        map.put(XMLNS_ATTRIBUTE, XMLNS_ATTRIBUTE_NS_URI);
        return map;
    }

    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null prefix");
        }
        if (PREFIX_TO_NAMESPACE_URI_MAP.containsKey(prefix)) {
            return PREFIX_TO_NAMESPACE_URI_MAP.get(prefix);
        } else {
            return NULL_NS_URI;
        }
    }

    @Override
    public String getPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Null namespaceURI");
        }

        final Collection<String> prefixes = NAMESPACE_URI_TO_PREFIX_MAP.get(namespaceURI);
        if (prefixes == null || prefixes.isEmpty()) {
            return null;
        } else {
            return prefixes.iterator().next();
        }
    }

    @Override
    public Iterator<?> getPrefixes(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Null namespaceURI");
        }
        final Collection<String> prefixes = NAMESPACE_URI_TO_PREFIX_MAP.get(namespaceURI);
        if (prefixes == null || prefixes.isEmpty()) {
            return Collections.emptyList().iterator();
        } else {
            return prefixes.iterator();
        }
    }

}