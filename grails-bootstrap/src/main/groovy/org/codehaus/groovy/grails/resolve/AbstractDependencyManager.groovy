/* Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.resolve

import groovy.transform.CompileStatic
import groovy.util.slurpersupport.GPathResult
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

import javax.xml.parsers.ParserConfigurationException
import java.util.concurrent.ConcurrentHashMap

/**
 * Abstract implementation of DependencyManager interface
 *
 * @author Graeme Rocher
 * @since 2.3
 */
//@CompileStatic
abstract class AbstractDependencyManager implements DependencyManager{

    private Map<File,GPathResult> parsedXmlCache = new ConcurrentHashMap<File, GPathResult>();

    GPathResult downloadPluginList(File localFile) {
        GPathResult parsedXml = parsedXmlCache[localFile]
        if (!parsedXml) {
            try {
                URL url = new URL(GRAILS_CENTRAL_PLUGIN_LIST)

                if (localFile.lastModified() < url.openConnection().lastModified ) {
                    localFile.withOutputStream { OutputStream os ->
                        url.withInputStream { InputStream is ->
                            os << is
                        }
                    }
                }

                def xmlSlurper = new XmlSlurper()
                xmlSlurper.setErrorHandler(new ErrorHandler() {
                    public void warning(SAXParseException e) throws SAXException {
                        // noop
                    }

                    public void error(SAXParseException e) throws SAXException {
                        // noop
                    }

                    public void fatalError(SAXParseException e) throws SAXException {
                        // noop
                    }
                });
                parsedXml = xmlSlurper.parse(localFile)
                parsedXmlCache.put(localFile, parsedXml)
            }
            catch (IOException e) {
                // ignore
            }
            catch (SAXException e) {
                // ignore
            }
            catch (ParserConfigurationException e) {
                // ignore
            }
        }
        return parsedXml
    }
}