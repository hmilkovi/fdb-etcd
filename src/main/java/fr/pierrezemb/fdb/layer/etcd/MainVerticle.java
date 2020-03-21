package fr.pierrezemb.fdb.layer.etcd;

import fr.pierrezemb.fdb.layer.etcd.impl.KVImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {


    String clusterFilePath = this.context.config().getString("fdb-cluster-file");
    System.out.println("connecting to fdb@" + clusterFilePath);

    EtcdRecordStore recordStore = new EtcdRecordStore(clusterFilePath);

    VertxServer server = VertxServerBuilder
      .forAddress(vertx, "localhost", 8080)
      .addService(new KVImpl(recordStore)).build();

    server.start(ar -> {
      if (ar.succeeded()) {
        System.out.println("gRPC service started");
        startPromise.complete();
      } else {
        System.out.println("Could not start server " + ar.cause().getMessage());
        startPromise.fail(ar.cause());
      }
    });
  }
}