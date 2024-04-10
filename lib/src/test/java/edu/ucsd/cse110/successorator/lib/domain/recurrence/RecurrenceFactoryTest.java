package edu.ucsd.cse110.successorator.lib.domain.recurrence;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class RecurrenceFactoryTest {
    RecurrenceFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new RecurrenceFactory();
    }

    @Test
    public void createRecurrence() {
        LocalDate now = LocalDate.now();
        assertEquals(Daily.class, factory.createRecurrence(now, RecurrenceFactory.RecurrenceEnum.DAILY).getClass());
        assertEquals(Weekly.class, factory.createRecurrence(now, RecurrenceFactory.RecurrenceEnum.WEEKLY).getClass());
        assertEquals(Monthly.class, factory.createRecurrence(now, RecurrenceFactory.RecurrenceEnum.MONTHLY).getClass());
        assertEquals(Yearly.class, factory.createRecurrence(now, RecurrenceFactory.RecurrenceEnum.YEARLY).getClass());
    }
}