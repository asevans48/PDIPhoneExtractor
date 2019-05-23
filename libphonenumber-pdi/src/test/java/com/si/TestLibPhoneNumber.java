package com.si;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.junit.jupiter.api.Test;

class TestLibPhoneNumber{

    @Test
    public void shouldFindPhoneNumbersInText() throws NumberParseException{
        PhoneNumber numberProto = PhoneNumberUtil.getInstance().parse("303 802 4561", "US");
        assert(PhoneNumberUtil.getInstance().isValidNumber(numberProto));
        assert(numberProto.getNationalNumber() == 3038024561L);
        numberProto = PhoneNumberUtil.getInstance().parse("303 451-1234", "US");
        assert(PhoneNumberUtil.getInstance().isValidNumber(numberProto));
        assert(numberProto.getNationalNumber() == 3034511234L);
        String text = "My phone number is (303)802-4561. Yours is 303 451-1234";
        Iterable<PhoneNumberMatch> numbers = PhoneNumberUtil.getInstance().findNumbers(text, "US");
        int i = 0;
        for(PhoneNumberMatch match : numbers){
            if(i == 0){
                assert(match.number().getNationalNumber() == 3038024561L);
            }else if(i == 1){
                assert(match.number().getNationalNumber() == 3034511234L);
            }
            i += 1;
        }
        assert(i == 2);
    }

    @Test
    public void shouldStandardizeValidUSPhoneNumber() throws NumberParseException{
        PhoneNumber numberProto = PhoneNumberUtil.getInstance().parse("303 411 4561", "US");
        assert(numberProto.getNationalNumber() == 3034114561L);
    }

    @Test
    public void shouldValidateValidUSPhoneNumber() throws NumberParseException {
        PhoneNumber numberProto = PhoneNumberUtil.getInstance().parse("303-411-4561", "US");
        assert(PhoneNumberUtil.getInstance().isValidNumber(numberProto));
    }

    @Test
    public void shouldNotValidateInvalidUSPhoneNumber() throws NumberParseException{
        PhoneNumber numberProto = PhoneNumberUtil.getInstance().parse("303-123-4561", "US");
        assert(!PhoneNumberUtil.getInstance().isValidNumber(numberProto));
    }

    @Test
    public void shouldReturnUSCountryCode() throws NumberParseException{
        PhoneNumber numberProto = PhoneNumberUtil.getInstance().parse("303-411-4561", "US");
        assert(PhoneNumberUtil.getInstance().isValidNumber(numberProto));
        assert(numberProto.getCountryCode() == 1);
    }
}
