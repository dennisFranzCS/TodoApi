pipeline {
  agent {
    node {
      label 'docker-agent'
    }
  }
  options {
    buildDiscarder logRotator(
      artifactDaysToKeepStr: '',
      artifactNumToKeepStr: '',
      daysToKeepStr: '1',
      numToKeepStr: '1'
    )
    timestamps()
    timeout(10)
    warnError('Exception caught')
  }
  triggers {
    pollSCM 'H/5 * * * *'
  }
  stages {
    stage('Build') {
      steps {
        echo "Building with Branch: ${GIT_BRANCH}"
      }
    }
    stage('Test') {
      steps {
        echo 'Testing...'
      }
    }
    stage('Deliver') {
      when { tag 'release-*' }
      steps {
        echo 'Deliver...'
      }
    }
  }
}
