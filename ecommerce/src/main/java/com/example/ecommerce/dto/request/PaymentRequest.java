package com.example.ecommerce.dto.request;

import com.example.ecommerce.entity.PaymentMethod;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentRequest {
    private PaymentMethod paymentMethod;
}
