def call(Map args){
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
             stage ('websiteCI - Build/Push docker image') {
                  when{
                    expression {
                      return args.build_type == "Release";
                    }
                  }
                  steps{
                    sh """ 
                    docker build -t sadhorse22/${args.name} .
                    docker push sadhorse22/${args.name}
                    """ 
                  }	
               }
              stage('Trigger Deployment') {
                  when{
                    expression {
                      return args.build_type == "Release";
                    }
                  }
                steps {
                     build job: "websiteContinuousDelivery", wait: false, parameters: [string(name: 'name', value: String.valueOf(name))]
                  }
               }
          }
       }
   }

