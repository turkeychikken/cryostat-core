/*
 * Copyright The Cryostat Authors
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or data
 * (collectively the "Software"), free of charge and under any and all copyright
 * rights in the Software, and any and all patent rights owned or freely
 * licensable by each licensor hereunder covering either (i) the unmodified
 * Software as contributed to or provided by such licensor, or (ii) the Larger
 * Works (as defined below), to deal in both
 *
 * (a) the Software, and
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software (each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 * The above copyright notice and either this complete permission notice or at
 * a minimum a reference to the UPL must be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.cryostat.core.net;

import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

import org.openjdk.jmc.rjmx.ConnectionDescriptorBuilder;
import org.openjdk.jmc.rjmx.ConnectionException;
import org.openjdk.jmc.rjmx.ConnectionToolkit;

import io.cryostat.core.sys.Environment;
import io.cryostat.core.sys.FileSystem;
import io.cryostat.core.tui.ClientWriter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class JFRConnectionToolkit {

    private final ClientWriter cw;
    private final FileSystem fs;
    private final Environment env;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "no mutable fields can be accessed through this class")
    public JFRConnectionToolkit(ClientWriter cw, FileSystem fs, Environment env) {
        this.cw = cw;
        this.fs = fs;
        this.env = env;
    }

    public JFRConnection connect(JMXServiceURL url)
            throws ConnectionException, IllegalStateException {
        return connect(url, null);
    }

    public JFRConnection connect(JMXServiceURL url, Credentials credentials)
            throws ConnectionException, IllegalStateException {
        return connect(url, credentials, List.of());
    }

    public JFRConnection connect(
            JMXServiceURL url, Credentials credentials, List<Runnable> listeners)
            throws ConnectionException, IllegalStateException {
        ConnectionDescriptorBuilder connectionDescriptorBuilder = new ConnectionDescriptorBuilder();
        connectionDescriptorBuilder = connectionDescriptorBuilder.url(url);
        if (credentials != null) {
            connectionDescriptorBuilder =
                    connectionDescriptorBuilder
                            .username(credentials.getUsername())
                            .password(credentials.getPassword());
        }
        return new JFRJMXConnection(cw, fs, env, connectionDescriptorBuilder.build(), listeners);
    }

    public String getHostName(JMXServiceURL url) {
        return ConnectionToolkit.getHostName(url);
    }

    public int getPort(JMXServiceURL url) {
        return ConnectionToolkit.getPort(url);
    }

    public JMXServiceURL createServiceURL(String host, int port) throws MalformedURLException {
        return ConnectionToolkit.createServiceURL(host, port);
    }

    public int getDefaultPort() {
        return ConnectionToolkit.getDefaultPort();
    }

    public MemoryMXBean getMemoryBean(MBeanServerConnection server) throws IOException {
        return ConnectionToolkit.getMemoryBean(server);
    }

    public RuntimeMXBean getRuntimeBean(MBeanServerConnection server) throws IOException {
        return ConnectionToolkit.getRuntimeBean(server);
    }

    public ThreadMXBean getThreadBean(MBeanServerConnection server) throws IOException {
        return ConnectionToolkit.getThreadBean(server);
    }

    public OperatingSystemMXBean getOperatingSystemBean(MBeanServerConnection server)
            throws IOException {
        return ConnectionToolkit.getOperatingSystemBean(server);
    }
}
