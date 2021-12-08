def call(Map args){
       stages {
           stage ('websiteCD - check minikube status') {
                steps{
                    sh """
                    echo 'check status'
                    if minikube status | grep -q 'Running'; 
                        then echo "matched"
                    else
                    minikube start
                    fi
                    """
                }
           }
           stage ('websiteCD - delete existing pod') { 
                steps{
                    sh """ 
                    if kubectl get pods | grep ${args.name}
                        then kubectl delete pod ${args.name}
                    fi
                    """ 
                }

           }
             stage ('websiteCD - run new pod') {
                  steps{
                    sh """ 
                    kubectl run ${args.name} --image=sadhorse22/${args.name}
                    """ 
                  }	
               }
              stage ('websiteCD - expose new pod') {
                    steps {
                        sh """ 
                        if kubectl get svc | grep ${args.name}
                            then kubectl delete svc ${args.name}
                        fi
                        kubectl expose pod ${args.name} --type=NodePort --port=80 --target-port=8080
                        """ 
                    } 
               }
                stage ('websiteCD - get website url') {
                    steps{
                        sh """ 
                        minikube ip
                        kubectl get service ${args.name}
                        """ 
                    }
                }
          }
       }

