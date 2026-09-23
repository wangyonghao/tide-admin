package top.wyhao.starter.core.util.validation;

import org.junit.jupiter.api.Test;
import top.wyhao.starter.core.exception.BizException;

import static org.junit.jupiter.api.Assertions.*;

public class CheckTest {

    @Test
    public void testWhenMessage_throwsWhenConditionTrue() {
        BizException e = assertThrows(BizException.class, () -> Check.when(true, "值 [{}] 非法", "x"));
        assertEquals("值 [x] 非法", e.getMessage());
    }

    @Test
    public void testWhenMessage_passesWhenConditionFalse() {
        assertDoesNotThrow(() -> Check.when(false, "不应抛出"));
    }

    @Test
    public void testWhenSupplier_throwsWhenConditionTrue() {
        assertThrows(BizException.class, () -> Check.when(() -> true, "应抛出"));
    }

    @Test
    public void testWhenSupplier_passesWhenConditionFalse() {
        assertDoesNotThrow(() -> Check.when(() -> false, "不应抛出"));
    }

    @Test
    public void testWhenException_throwsWhenConditionTrue() {
        IllegalStateException ex = new IllegalStateException("boom");
        assertSame(ex, assertThrows(IllegalStateException.class, () -> Check.when(true, ex)));
    }

    @Test
    public void testWhenException_passesWhenConditionFalse() {
        assertDoesNotThrow(() -> Check.when(false, new IllegalStateException("boom")));
    }
}
