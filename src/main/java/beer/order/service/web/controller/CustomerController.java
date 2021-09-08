package beer.order.service.web.controller;

import beer.order.service.domain.Customer;
import beer.order.service.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/customers")
@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/")
    public ResponseEntity<List<Customer>> getCustomer(){
        return ResponseEntity.ok(customerService.getCustomers());
    }

}
