FROM python:3
WORKDIR /app
COPY . /app
RUN pip3 install -U pip Flask psutil requests
EXPOSE 8001
CMD ["python3", "app.py", "8001"]