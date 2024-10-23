package repository.tests;

import org.example.model.PaymentMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRepositoryTest extends BaseRepositoryTest {

    @BeforeAll
    static void setUp() throws SQLException {
        setUpBase();
    }

    @Test
    void canFindAllPayments() {
        List<PaymentMethod> payments = paymentRepository.findAll();
        assertThat(payments.size()).isEqualTo(14);
        assertThat(payments.get(0).getName()).isEqualTo("Company Credit Card");
        assertThat(payments.get(1).getName()).isEqualTo("Personal Credit Card");
    }

    @Test
    void canGetPaymentByID() {
        Optional<PaymentMethod> payment = paymentRepository.findByID(2L);
        assertThat(payment).isPresent();
        assertThat(payment.get().getId()).isEqualTo(2);
        assertThat(payment.get().getName()).isEqualTo("Personal Credit Card");
    }

    @Test
    void cannotGetPaymentByInvalidID() {
        Optional<PaymentMethod> payment1 = paymentRepository.findByID(55L);
        assertThat(payment1).isEmpty();
    }
}
