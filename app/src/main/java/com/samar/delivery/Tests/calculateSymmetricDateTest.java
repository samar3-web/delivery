package com.samar.delivery.Tests;

//import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.samar.delivery.HomeActivity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class calculateSymmetricDateTest {


    @Test
    public void testCalculateSymmetricDateWithCurrentDate() {
        Date currentDate = new Date();
        Date symmetricDate = HomeActivity.calculateSymmetricDate(currentDate);

        // La date symétrique devrait être égale à la date actuelle
        assertEquals(currentDate.toString(), symmetricDate.toString());
    }
    @Test
    public void testCalculateSymmetricDateWithFutureDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date futureDate = dateFormat.parse("2023-12-31T20:15:00");
        Date symmetricDate = HomeActivity.calculateSymmetricDate(futureDate);

        // La date symétrique devrait être dans le passé
        assertTrue(symmetricDate.before(new Date()));
    }
    @Test
    public void testCalculateSymmetricDateWithPastDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date pastDate = dateFormat.parse("2020-01-01T06:30:00");
        Date symmetricDate = HomeActivity.calculateSymmetricDate(pastDate);

        // La date symétrique devrait être dans le futur
        assertTrue(symmetricDate.after(new Date()));
    }
    @Test
    public void testCalculateSymmetricDateWithNullDate() {
        Date currentDate = new Date();
       // assertNull(HomeActivity.calculateSymmetricDate(null));
        assertEquals(currentDate.toString(), HomeActivity.calculateSymmetricDate(null).toString());
    }

}
