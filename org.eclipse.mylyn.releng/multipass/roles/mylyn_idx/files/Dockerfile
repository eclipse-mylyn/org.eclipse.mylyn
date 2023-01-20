FROM python:3
ARG APP_VERSION=container
ENV APP_VERSION=$APP_VERSION
WORKDIR /app
COPY ./app_$APP_VERSION.py /app
RUN mv /app/app_$APP_VERSION.py /app/app.py && pip3 install -U pip Flask psutil requests
EXPOSE 8001
CMD ["python3", "app.py", "8001"]