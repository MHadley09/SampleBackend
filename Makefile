TAG = `git rev-parse --short HEAD`

build:
	docker build -t sample-backend:$(TAG) .
	docker tag sample-backend:$(TAG) sample/sample-backend:latest
	docker tag sample-backend:$(TAG) sample/sample-backend:$(TAG)
	echo "Built sample-backend:$(TAG)"

deploy: build
	docker push sample/sample-backend:$(TAG)
	echo "Pushed sample/sample-backend:$(TAG)"

test:
	echo $(C)
