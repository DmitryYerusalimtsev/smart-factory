# smart-factory

Run Apache Ignite server:

<code>
docker run -d -v /data-processing-pipeline/src/main/resources/ignite-config.xml:/ignite-config.xml -e CONFIG_URI=/ignite-config.xml -p 47500:47500 -p 11211:11211 -p 47100:47100 -p 49112:49112 -p 10800:10800 apacheignite/ignite
</code>