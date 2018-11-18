# Changelog

## 0.0.3-SNAPSHOT - 2018-11-18

- Move UI module's Apache configuration from Dockerfile to `httpd.conf` file
- Update Apache's configuration:
  - Set allowed HTTP methods to `GET` and `HEAD`
  - Add `X-Frame-Options`, `X-XSS-Protection` and `X-Content-Type-Options`
  headers to responses
  - Remove `FileETag` header from responses
  - Modify `ServerSignature` and `ServerTokens` values

  X-Frame-Options "deny"

  Header set X-XSS-Protection "1; mode=block"

  Header set X-Content-Type-Options nosniff

## 0.0.3-SNAPSHOT - 2018-11-15

- Add support for defining proxy URL using a Java property (`-Dproxy=http://<HOST>`)
- Add support for defining DB connection string using a Java property (`-Ddb.connectionString=mongodb://db:27017/xrde2emonitoring?safe=true`)
- Update Spring Boot from version `1.4.2` to version `2.1.0`

## 0.0.3-SNAPSHOT - 2018-11-10

- Update XRd4J dependency from `com.pkrete.xrd4j` to `org.niis.xrd4j`
- Update XRd4J version from `0.0.16` to `0.3.0`
- Add OWASP dependency check Maven plugin
- Add Checkstyle Maven plugin and configuration
- Fix Checkstyle errors
- Fix SonarQube findings
- Add `CHANGELOG.md`
