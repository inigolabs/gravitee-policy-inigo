build:
	mvn prettier:write validate generate-sources package -Dmaven.test.skip -U
	rm -r plugins/.work >> /dev/null 2>&1 || true
	mv target/gravitee-policy-inigo-1.0.0.zip plugins/

build-libinigo:
	cd ../.. && make build
	cd ../../../inigo/go && go run -ldflags=-checklinkname=0 igo/main.go build -f ffi inigo-linux-arm64
	cp -f ../../../inigo/dist_ffi/inigo_linux_arm64/libinigo-linux-arm64.so plugins/libinigo.so

docker-up:
	docker compose -f docker-compose.yml up

docker-down:
	docker compose -f docker-compose.yml down

run: docker-down build docker-up

restart-gateway:
	docker compose -f docker-compose.yml restart gateway