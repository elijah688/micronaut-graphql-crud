.PHONY: run build clean stop docker

watch:
	./gradlew compileJava --continuous --parallel --build-cache --configuration-cache

run:
	./gradlew run --continuous --no-configuration-cache

clean:
	./gradlew clean  --refresh-dependencies --no-configuration-cache

stop:
	./gradlew --stop


DB_CONTAINER_NAME := test-db
DB_IMAGE          := postgres:15
DB_PORT           := 5432
DB_USER           := test
DB_PASSWORD       := test
DB_NAME           := testdb

db_up:
	@echo ">>> Starting Postgres container '$(DB_CONTAINER_NAME)'..."
	docker run -d \
	  --name $(DB_CONTAINER_NAME) \
	  -e POSTGRES_USER=$(DB_USER) \
	  -e POSTGRES_PASSWORD=$(DB_PASSWORD) \
	  -e POSTGRES_DB=$(DB_NAME) \
	  -p $(DB_PORT):5432 \
	  $(DB_IMAGE) \
	&& echo "Waiting for Postgres to be ready…" \
	&& until docker exec $(DB_CONTAINER_NAME) pg_isready -U $(DB_USER) > /dev/null 2>&1; do sleep 1; done \
	&& echo "Postgres is ready!"

db_down:
	@echo ">>> Tearing down Postgres container '$(DB_CONTAINER_NAME)'..."
	docker rm -f $(DB_CONTAINER_NAME) > /dev/null 2>&1 || true
test:
	@echo ">>> Running tests with isolated DB…"
	@$(MAKE) db_up
	@{ \
		./gradlew clean test --rerun-tasks --no-build-cache --info; \
		EXIT_CODE=$$?; \
		echo ">>> Tests complete (exit $$EXIT_CODE), tearing down DB…"; \
		$(MAKE) db_down; \
		exit $$EXIT_CODE; \
	}


docker::
	./gradlew clean dockerBuildNative --no-build-cache; \
	docker compose -f build.yaml up -d
