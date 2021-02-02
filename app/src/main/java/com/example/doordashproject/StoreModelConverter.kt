package com.example.doordashproject


import com.squareup.moshi.*

class StoreModelConverter() {

    // a toJson definition is mandatory (NO-OP)
    @ToJson fun toJson(model: StoreModel) : Boolean { return false }

    @Suppress("unused")
    @FromJson
    fun fromJson(reader: JsonReader): StoreModel {
        val moshi = Moshi.Builder().build()
        val storeModel = StoreModel(Const.DEFAULT_ID)
        var options = optionsAsStrings(Json.discoverOptions)

        reader.beginObject()

        if (reader.peekJson().hasNext() && reader.peekJson().nextName() == Const.PHONE_NUMBER) {
            options = optionsAsStrings(Json.detailsOptions)
        }

        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    storeModel.address = moshi.adapter(Address::class.java)
                        .fromJson(reader) ?: Address()
                }
                // asap_minute_range
                1 -> storeModel.averageRating = reader.nextString()
                2 -> storeModel.coverImgUrl = reader.nextString()
                3 -> storeModel.description = reader.nextString()
                4 -> storeModel.distanceFromConsumer = reader.nextDouble()
                5 -> storeModel.id = reader.nextInt()
                // lat, lng
                6 -> storeModel.name = reader.nextString()
                7 -> storeModel.nextCloseTime = reader.nextString()
                8 -> storeModel.nextOpenTime = reader.nextString()
                9 -> storeModel.offersDelivery = reader.nextBoolean()
                10 -> storeModel.offersPickup = reader.nextBoolean()
                11 -> storeModel.phoneNumber = reader.nextString()
                // printable_address
                12 -> {
                    storeModel.status = moshi.adapter(StoreStatus::class.java)
                        .fromJson(reader) ?: StoreStatus()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return storeModel
    }

    private fun optionsAsStrings(options: List<Int>) : JsonReader.Options {
        return JsonReader.Options
            .of(*Json.moshiMap.mapIndexed { i, name ->
                if (options.contains(i)) name
                else Json.randomNames[i]
            }.toTypedArray())
    }
}