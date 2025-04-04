def call(String imageName, String imageVersion){
  sh "docker build -t ${imageName}:${imageVersion} ."
}
