# Base Apache 2.4
FROM httpd:2.4

# Copy the UI files under Apache's htdocs
COPY src/ /usr/local/apache2/htdocs/

# Load proxy module
RUN sed -i 's/^#\(LoadModule proxy_module\)/\1/g' /usr/local/apache2/conf/httpd.conf
RUN sed -i 's/^#\(LoadModule proxy_http_module\)/\1/g' /usr/local/apache2/conf/httpd.conf

# Configure reverse proxy
RUN echo '<IfModule mod_proxy.c>' >> /usr/local/apache2/conf/httpd.conf
RUN echo 'ProxyRequests On' >> /usr/local/apache2/conf/httpd.conf
RUN echo 'ProxyPass /ajax/ http://xrde2e-backend:8080/' >> /usr/local/apache2/conf/httpd.conf
RUN echo 'ProxyPassReverse /ajax/ http://xrde2e-backend:8080/' >> /usr/local/apache2/conf/httpd.conf
RUN echo '</IfModule>' >> /usr/local/apache2/conf/httpd.conf

EXPOSE 80
CMD ["httpd-foreground"]