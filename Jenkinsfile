node ('master'){
   // Mark the code checkout 'stage'....
   stage 'Checkout'
   checkout scm

   stage 'Build application and Run Unit Test'

   def mvnHome = tool 'M3'
   sh "${mvnHome}/bin/mvn clean package"

   step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])


   stage 'Build Docker image'

   def image = docker.build('infinityworks/dropwizard-example:snapshot', '.')

   stage 'Acceptance Tests'
   image.withRun('-p 8181:8080') {c ->
        sh "${mvnHome}/bin/mvn verify"
   }

   /* Archive acceptance tests results */
   step([$class: 'JUnitResultArchiver', testResults: '**/target/failsafe-reports/TEST-*.xml'])

   stage 'Run SonarQube analysis'
   sh "${mvnHome}/bin/mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent test"
   sh "${mvnHome}/bin/mvn package sonar:sonar -Dsonar.host.url=http://ec2-54-171-187-14.eu-west-1.compute.amazonaws.com:9000"

   input "Does http://10.42.85.36:9000/dashboard/index/io.dropwizard:dropwizard-example look good?"

   stage 'Push image'

   docker.withRegistry("https://registry.infinityworks.com", "docker-registry") {
      //tag=sh "\$(git rev-parse --short HEAD)"
      image.tag("latest", false)
      image.push()
   }

}
