def call(String build_type, String name, String image_type){
  pipeline {
       agent any
       stages {
           stage ('websiteCI - Checkout') {
                steps{
                    checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '0a1990f2-e10e-49c1-a634-deb01976d44b', url: 'https://github.com/shauryaBlue/website.git']]]) 
                    sh """
                    pwd
                    """
                }
           }
           stage ('websiteCI - Build war file') {
                steps{
                    sh """ 
                    rm -f /.idea
                    rm -rf target/
                    mvn clean install
                    """ 
                }

           }
           if(build_type == "Release"){
             stage ('websiteCI - Build/Push docker image') { 
                  steps{
                    sh """ 
                    docker build -t sadhorse22/${name} .
                    docker push sadhorse22/${name}
                    """ 
                  }	
               }
              stage('Trigger Deployment') {
                steps {
                     build job: "websiteContinuousDelivery", wait: false, parameters: [string(name: 'name', value: String.valueOf(name))]
                  }
               }
          }
       }
   }
}
