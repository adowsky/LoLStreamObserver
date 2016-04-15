package com.adowsky.data.lol;

/**
 *
 * @author adowsky
 */
public enum LoLServer {
    na{
        @Override
        public String restId(){
            return "NA1";
        }
    }, 
    euw{
        @Override
        public String restId(){
            return "EUW1";
        }
        
    }, 
    eune{
        @Override
        public String restId(){
            return "EUN1";
        }
    }, 
    br{
        @Override
        public String restId(){
            return "BR1";
        }
    }, 
    jp{
        @Override
        public String restId(){
            return "JP1";
        }
    }, 
    kr{
        @Override
        public String restId(){
            return "KR";
        }
    }, 
    las{
        @Override
        public String restId(){
            return "LA1";
        }
    }, 
    oce{
        @Override
        public String restId(){
            return "OC1";
        }
    }, 
    ru{
        @Override
        public String restId(){
            return "RU";
        }
    },
    tr{
       @Override
        public String restId(){
            return "TR1";
        } 
    }; 
    
    public abstract String restId();
}
