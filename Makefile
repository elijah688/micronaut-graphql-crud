.PHONY: run build clean stop

watch:
	./gradlew compileJava --continuous --parallel --build-cache --configuration-cache

run:
	./gradlew run --continuous 

clean:
	./gradlew clean --refresh-dependencies

stop:
	./gradlew --stop

