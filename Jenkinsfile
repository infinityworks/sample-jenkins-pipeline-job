node {
   // Mark the code checkout 'stage'....
   stage 'Checkout'
   checkout scm

   // workaround to let user jenkins to run docker commands without sudo as not supported by the cloudbee plugin
   sh "sudo chown jenkins /var/run/docker.sock"
   sh "sudo chown jenkins /usr/bin/docker"

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
   sh "${mvnHome}/bin/mvn clean test sonar:sonar -Dsonar.host.url=http://sonar:9000"

   stage 'Push image'
   docker.withRegistry("http://10.42.136.56:8081/repository/registry.nexus/", "nexus-registry") {
          image.push()
   }


}
