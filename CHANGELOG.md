# Changelog

## 0.0.3-SNAPSHOT - 2018-11-15
`
- Add support for defining proxy URL using a Java property (`-Dproxy=http://<HOST>`)
- Add support for defining DB connection string using a Java property (`-Ddb.connectionString=mongodb://db:27017/xrde2emonitoring?safe=true`)

## 0.0.3-SNAPSHOT - 2018-11-10
`
- Update XRd4J dependency from `com.pkrete.xrd4j` to `org.niis.xrd4j`
- Update XRd4J version from `0.0.16` to `0.3.0`
- Add OWASP dependency check Maven plugin
- Add Checkstyle Maven plugin and configuration
- Fix Checkstyle errors
- Fix SonarQube findings
- Add `CHANGELOG.md`