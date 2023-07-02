package dev.fangscl.CloudProviders.aws;

import dev.fangscl.Resources.ResourceCallable;
import dev.fangscl.Resources.Vm;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

@Log4j2
public class Provider implements ResourceCallable {
    private final FileWriter writer;
    private final Ec2Client ec2Client = Ec2Client.builder()
            .region(Region.EU_CENTRAL_1)
            .build();

    public Provider() {
        try {
            writer = new FileWriter("state", StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object apply(String name, Map<String, Object> args) {
//        try {
//            var resource = (Vm) mapper.convertValue(args, Class.forName("dev.fangscl.Resources.Vm"));
//
//            var read = Files.readString(Path.of("state"));
//
//
//            if (read.length() > 0) {
//                var stateVm = mapper.readValue(read, Vm.class);
//                if (stateVm != null) {
//                    ObjectReader updater = mapper.readerForUpdating(stateVm);
////                    var merge = updater.readValue(resource, Vm.class);
//                    var cloudResVm = describeEC2Instance(stateVm.id()).get(0);
//                    var cloud = mapper.convertValue(cloudResVm, Vm.class);
//
//                    var cloudNode = mapper.valueToTree(cloud);
//                    var stateVmNode = mapper.valueToTree(stateVm);
//                    var patch = JsonDiff.asJsonPatch(cloudNode, stateVmNode);
//
//                    System.out.println(patch);
//                } else {
//                    create(resource);
//                }
//            } else {
//                create(resource);
//            }
//
//            writer.write(mapper.writeValueAsString(resource));
//            writer.flush();
//            writer.close();
//        } catch (ClassNotFoundException | IOException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    private void create(Vm resource) {
        var run_request = RunInstancesRequest.builder()
                .imageId("ami-0b2ac948e23c57071")
                .instanceType(InstanceType.T3_MICRO)
                .tagSpecifications(TagSpecification.builder().resourceType(ResourceType.INSTANCE).tags(Tag.builder().key("Name")
                        .value(resource.name()).build()).build())
                .maxCount(resource.maxCount())
                .minCount(resource.minCount())
                .build();
        var resp = ec2Client.runInstances(run_request);
        Instance instance = resp.instances().get(0);

        resource.imageId(instance.imageId());
        resource.id(instance.instanceId());
    }

    public ArrayList<Instance> describeEC2Instance(String... ids) {
        var array = new ArrayList<Instance>();
        try {
            String nextToken = null;

            do {
                var request = DescribeInstancesRequest.builder().instanceIds(ids).nextToken(nextToken).build();
                var response = ec2Client.describeInstances(request);


                for (Reservation reservation : response.reservations()) {
                    array.addAll(reservation.instances());
                }
                nextToken = response.nextToken();
            } while (nextToken != null);
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return array;
    }
}
