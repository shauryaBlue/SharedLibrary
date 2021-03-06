def call(Map args){
        node{
           stage ('websiteCD - check minikube status') {
                    sh """
                    echo 'check status'
                    if minikube status | grep -q 'Running'; 
                        then echo "matched"
                    else
                    minikube start
                    fi
                    """       
           }
           stage ('websiteCD - delete existing pod') { 
                    sh """ 
                    if kubectl get pods | grep ${args.name}
                        then kubectl delete pod ${args.name}
                    fi
                    """ 
           }
             stage ('websiteCD - run new pod') { 
                    sh """ 
                    kubectl run ${args.name} --image=sadhorse22/${args.name}
                    """ 
                  }
              stage ('websiteCD - expose new pod') {
                      if(args.image_type == "Java"){
                          sh """ 
                           if kubectl get svc | grep ${args.name}
                              then kubectl delete svc ${args.name}
                           fi
                            kubectl expose pod ${args.name} --type=NodePort --port=8080 --target-port=8080
                          """ 
                      }
                      else {
                          sh """ 
                            if kubectl get svc | grep ${args.name}
                              then kubectl delete svc ${args.name}
                            fi
                            kubectl expose pod ${args.name} --type=NodePort --port=5000 --target-port=5000
                          """ 
                      }

                    }
                stage ('websiteCD - get website url') {    
                        sh """ 
                        minikube ip
                        kubectl get service ${args.name}
                        """ 
                    }
                }                      
            }
         

