FROM reg.bitgadak.com/closememo/command-base:0.1

EXPOSE 10081

RUN mkdir -p /home/deployer/deploy
RUN mkdir -p /home/deployer/logs
COPY ./build/libs/command.jar /home/deployer/deploy

ENTRYPOINT java -jar -Dspring.profiles.active=$PROFILE /home/deployer/deploy/command.jar
