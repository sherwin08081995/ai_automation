pipeline {
  agent any
  // tools { jdk 'JDK17'; maven 'M3' } // uncomment if configured in Jenkins

  // 11:00, 14:00, 17:00, 20:00, 23:00 IST
  triggers { cron('0 11-23/3 * * *') }

  options {
    disableConcurrentBuilds()                      // queue if previous still running
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '30'))
    timeout(time: 4, unit: 'HOURS')               // your runs can be long
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Env sanity') {
      steps {
        bat 'echo [%DATE% %TIME%] 🔎 Env check'
        bat 'where mvn'
        bat 'java -version'
        bat 'dir'                                 // list repo root
        // Ensure suite file exists; fail fast if not
        bat 'if not exist testng.xml (echo ❌ testng.xml NOT FOUND & exit /b 2)'
      }
    }

    stage('Build & Test') {
      steps {
        bat 'echo [%DATE% %TIME%] ✅ Starting tests'
        // Run TestNG suite; -B batch mode, -U update snapshots, fail at end optional
        bat 'mvn -B -U clean test -DsuiteXmlFile=testng.xml -DskipTests=false'
        bat 'echo [%DATE% %TIME%] ✅ Tests finished'
      }
    }

    stage('Generate Allure Report') {
      // Only attempt if results exist
      when { expression { fileExists('target\\allure-results') || fileExists('allure-results') } }
      steps {
        bat 'echo [%DATE% %TIME%] 📊 Generating Allure report'
        bat 'mvn -B allure:report'
      }
    }
  }

  post {
    always {
      // Publish TestNG/JUnit results to Jenkins UI
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, **/testng-results.xml'

      // Archive useful artifacts
      archiveArtifacts artifacts: 'target/surefire-reports/**, target/allure-results/**, target/site/allure-maven-plugin/**, logs/**', allowEmptyArchive: true

      // If you have the Allure Jenkins plugin installed, you can also publish:
      // allure includeProperties: false, results: [[path: 'target/allure-results']]
    }
    success { echo '🎉 Run succeeded' }
    failure { echo '❌ Run failed — check Console Output' }
  }
}
