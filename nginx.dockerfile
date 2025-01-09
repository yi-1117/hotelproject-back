FROM nginx:1.26.2

ENV TZ="Aisa/Taipei"
EXPOSE 80/TCP
EXPOSE 6173/TCP

ARG publish_dir=dist
COPY ./${publish_dir}/ /usr/share/nginx/html

ARG config_file=nginx.conf
COPY ./${config_file} /etc/nginx/nginx.conf

CMD ["nginx", "-g", "daemon off;"]