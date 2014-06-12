Configuration Changes for Acquire 2.3:
Modules updated:
 org/hibernate/envers to 4.2.2
 org/hibernate/main to 4.2.2
 
Standalone-full.xml updates:
<subsystem xmlns="urn:jboss:domain:messaging:1.1">
            <hornetq-server>
                <persistence-enabled>true</persistence-enabled>
                <journal-file-size>102400</journal-file-size>
                <journal-min-files>2</journal-min-files>

                <connectors>
                    <netty-connector name="netty" socket-binding="messaging"/>
                    <netty-connector name="netty-throughput" socket-binding="messaging-throughput">
                        <param key="batch-delay" value="50"/>
                    </netty-connector>
                    <in-vm-connector name="in-vm" server-id="0"/>
                </connectors>

                <acceptors>
                    <netty-acceptor name="netty" socket-binding="messaging"/>
                    <netty-acceptor name="netty-throughput" socket-binding="messaging-throughput">
                        <param key="batch-delay" value="50"/>
                        <param key="direct-deliver" value="false"/>
                    </netty-acceptor>
                    <in-vm-acceptor name="in-vm" server-id="0"/>
                </acceptors>

                <security-settings>
                    <security-setting match="#">
                        <permission type="send" roles="guest"/>
                        <permission type="consume" roles="guest"/>
                        <permission type="createDurableQueue" roles="guest"/>
                        <permission type="deleteDurableQueue" roles="guest"/>
                        <permission type="createNonDurableQueue" roles="guest"/>
                        <permission type="deleteNonDurableQueue" roles="guest"/>
                    </security-setting>
                </security-settings>

                <address-settings>
                    <address-setting match="#">
                        <dead-letter-address>jms.queue.deadLetterQueue</dead-letter-address>
                        <expiry-address>jms.queue.ExpiryQueue</expiry-address>
                        <redelivery-delay>5000</redelivery-delay>
                        <max-delivery-attempts>3</max-delivery-attempts>
                        <max-size-bytes>10485760</max-size-bytes>
                        <page-size-bytes>1048576</page-size-bytes>
                        <address-full-policy>PAGE</address-full-policy>
                        <message-counter-history-day-limit>10</message-counter-history-day-limit>
                    </address-setting>
                </address-settings>

                <jms-connection-factories>
                    <connection-factory name="InVmConnectionFactory">
                        <connectors>
                            <connector-ref connector-name="in-vm"/>
                        </connectors>
                        <entries>
                            <entry name="java:/jms/JmsConnectionFactory"/>
                        </entries>
                    </connection-factory>
                    <connection-factory name="RemoteConnectionFactory">
                        <connectors>
                            <connector-ref connector-name="netty"/>
                        </connectors>
                        <entries>
                            <entry name="RemoteConnectionFactory"/>
                            <entry name="java:jboss/exported/jms/RemoteConnectionFactory"/>
                        </entries>
                    </connection-factory>
                    <pooled-connection-factory name="hornetq-ra">
                        <transaction mode="xa"/>
                        <connectors>
                            <connector-ref connector-name="in-vm"/>
                        </connectors>
                        <entries>
                            <entry name="java:/JmsXA"/>
                        </entries>
                        <client-id>acquire</client-id>
                    </pooled-connection-factory>
                </jms-connection-factories>

                <jms-destinations>
                    <jms-queue name="testQueue">
                        <entry name="queue/test"/>
                    </jms-queue>
                    <jms-queue name="deadLetterQueue">
                        <entry name="java:/deadLetterQueue"/>
                        <durable>true</durable>
                    </jms-queue>
                    <jms-queue name="scoreboardQueue">
                        <entry name="queue/scoreboard"/>
                        <durable>true</durable>
                    </jms-queue>
                    <jms-queue name="annotationUpdateQueue">
                        <entry name="queue/annotationUpdate"/>
                        <durable>true</durable>
                    </jms-queue>
                    <jms-queue name="naLabReportQueue">
                        <entry name="queue/naLabReport"/>
                        <durable>false</durable>
                    </jms-queue>
                    <jms-topic name="testTopic">
                        <entry name="topic/test"/>
                    </jms-topic>
                    <jms-topic name="newSpecimenTopic">
                        <entry name="topic/newSpecimen"/>
                    </jms-topic>
                    <jms-topic name="newParticipantTopic">
                        <entry name="topic/newParticipant"/>
                    </jms-topic>
                    <jms-topic name="userChangeTopic">
                        <entry name="topic/userChange"/>
                    </jms-topic>
                    <jms-topic name="newUserTopic">
                        <entry name="topic/newUser"/>
                    </jms-topic>
                    <jms-topic name="newSiteTopic">
                        <entry name="topic/newSite"/>
                    </jms-topic>
                    <jms-topic name="updateConsentTopic">
                        <entry name="topic/updateConsent"/>
                    </jms-topic>
                    <jms-topic name="updateSpecimenTopic">
                        <entry name="topic/updateSpecimen"/>
                    </jms-topic>
                    <jms-topic name="dynamicExtensionTopic">
                        <entry name="topic/dynamicExtension"/>
                    </jms-topic>
                </jms-destinations>
            </hornetq-server>
        </subsystem>
        