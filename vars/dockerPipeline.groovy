def call() {
    def pipeline = new org.lab3.DockerPipeline(this)
    pipeline.runPipeline()
}
