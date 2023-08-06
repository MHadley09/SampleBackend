TAG = `git rev-parse --short HEAD`

build:
	docker build -t sample-backend:$(TAG) .
	docker tag sample-backend:$(TAG) sample-tv/hello-ecs:latest
	docker tag sample-backend:$(TAG) sample-tv/sample-backend:$(TAG)
	echo "Built sample-backend:$(TAG)"

deploy: build
	docker push sample-tv/sample-backend:$(TAG)
	echo "Pushed sample-tv/sample-backend:$(TAG)"

test:
	echo $(C)