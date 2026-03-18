from diagrams import Cluster, Diagram
from diagrams.programming.framework import Spring
from diagrams.onprem.container import Docker
from diagrams.custom import Custom
from diagrams.aws.general import User

with Diagram("", show=False):
    spring_service = Spring("Daily Summary Service")
    ollama_node = Docker("ollama")
    news_api = Custom("News API", "./news-api.png")

    with Cluster("News Summary Service"):
        api = [news_api << spring_service >> ollama_node]

