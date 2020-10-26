package onlineshop;

import onlineshop.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Delivery(@Payload Paid paid){

        if(paid.isMe()){
            System.out.println("##### listener Delivery : " + paid.toJson());

            Delivery delivery = new Delivery();
            delivery.setOrderId(paid.getOrderId());
            delivery.setStatus("Deliveried");

            deliveryRepository.save(delivery);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCanceled_Ordercancel(@Payload PayCanceled payCanceled){
        try {
            if (payCanceled.isMe()) {
                //    System.out.println("##### listener Paycancel : " + payCanceled.toJson());
                List<Delivery> deliverytList = deliveryRepository.findByOrderId(payCanceled.getOrderId());
                for(Delivery delivery : deliverytList){
                    delivery.setStatus("DeliveryCanceled");
                    deliveryRepository.save(delivery);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //if(payCanceled.isMe()){
        //    System.out.println("##### listener Ordercancel : " + payCanceled.toJson());

        //    deliveryRepository.findById(payCanceled.getOrderId())
        //            .ifPresent(
        //                    delivery -> {
        //                        delivery.setStatus("ShipCanceled");
        //                        deliveryRepository.save(delivery);
        //                    }
        //            )
        //    ;

        //}
    }

}
