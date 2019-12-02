FROM navikt/java:12

COPY build/libs/*.jar ./

COPY init.sh /init-scripts/init.sh