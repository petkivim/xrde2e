# Base Apache 2.4
FROM httpd:2.4

# Copy the UI files under Apache's htdocs
COPY src/ /usr/local/apache2/htdocs/

# Copy Apache configuration
COPY conf/httpd.conf /usr/local/apache2/conf/httpd.conf

EXPOSE 80
CMD ["httpd-foreground"]
