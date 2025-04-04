## Jenkins Shared Library 
### Step 1: Create a GitHub Repository for the Library
- Create a GitHub repo, e.g., jenkins-shared-lib. jenkins-shared-lib/

jenkins-shared-library/
├── vars/
    └── clone.groovy
    └── dockerbuild.groovy
    └── dockerpush.groovy


#### clone.groovy syntax
```bash
def call(String url, String branch){
  git url: "${url}", branch: "${branch}"
}
```
#### dockerbuild.groovy
```bash
def call(String ProjectName, String ImageTag, String DockerHubUser){
  sh "docker build -t ${DockerHubUser}/${ProjectName}:${ImageTag} ."
}
```
#### dockerpush.groovy
```bash
def call(String Project, String ImageTag, String dockerhubuser){
  withCredentials([usernamePassword(credentialsId: 'dockerHubCred', passwordVariable: 'dockerHubPass', usernameVariable: 'dockerHubUser')]) {
      sh "docker login -u ${dockerhubuser} -p ${dockerhubpass}"
  }
  sh "docker push ${dockerhubuser}/${Project}:${ImageTag}"
}
```

### Step 2: Configure Jenkins to Use the Shared Library 
- Open Jenkins Dashboard → Manage Jenkins → Configure System.
- Scroll down to Global Pipeline Libraries and click Add.
- Fill out the fields:
    - Name: jenkins-shared-lib (must match the name used in @Library).
    - Default version: main (or whatever your Git branch is)
    - Retrieval method: Modern SCM
    - SCM: Git
        - Project Repository: https://github.com/vinayakz/two-tier-flask-app.git
        - Add credentials if it's a private repo. 

### Step 3: Use the Shared Library in Your Jenkinsfile 
- GoTo Jenkins create new Pipeline and copy from old pipline setup and create
- choose pipeline definition >> pipeline script
- follow below code for shared library

```bash
@Library('Shared') _
pipline{
  agent {lable 'dev-server'}

  stages{
    stage("Code clone"){
      stpes{
        clone("https://github.com/vinayakz/two-tier-flask-app.git","master")
      }
    }
    stage("Build"){
      steps{
        dockerbuild("notes-app","latest")
      }
    }
    stage("Push to DocekrHub"){
      steps{
        dockerpush("dockerHubCreds","notes-app","latest")
      }
    }
  }
}
```

## Why Use Jenkins shared Library 
Using a Jenkins Shared Library brings modularity, reusability, and clean structure to your CI/CD pipelines — especially as your projects or teams grow

1. Don’t Repeat Yourself
You can reuse the same functions (e.g., deploy steps, notifications, build logic) across multiple Jenkins pipelines without duplicating code.

Example: Define a deployToProd() step once and use it in 10+ pipelines.

2. Cleaner Jenkinsfiles
Instead of putting hundreds of lines in the Jenkinsfile, you can abstract complexity into the shared library and keep your pipelines easy to read.
