package com.voila.customtransfer;

import java.util.Objects;

public record Station(String id, String name) {
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Station station)) return false;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name);
    }
}
