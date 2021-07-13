require("lua.lib")

function parseContainer(name, vals)
    itemList = luajava.newInstance("java.util.ArrayList")

    rolls = 1
    if vals["rolls"] then
        rolls = tonumber(vals["rolls"])
    end

    lastItemName = ""
    for i,nameOrOdds in ipairs(vals["items"]) do
        if i%2 == 1 then
            lastItemName = lib.stripExtras(nameOrOdds)
        else
            itemList:add(luajava.newInstance("LuaJavaDefines.Item", lastItemName, tonumber(nameOrOdds)))
        end
    end
    return luajava.newInstance("LuaJavaDefines.Container", name, rolls, itemList, false)
end

function parseVehicle(name, vals)
    containers = luajava.newInstance("java.util.ArrayList")

    for k,v in pairs(vals) do
        containers:add(parseContainer(k,v))
    end

    return luajava.newInstance("LuaJavaDefines.Location", name, false, containers)
end


vehicles = luajava.newInstance("java.util.ArrayList")

for _,val in ipairs(VehicleDistributions) do
    for vehicleName,vals in pairs(val) do
        vehicles:add(parseVehicle(vehicleName, vals["Normal"]))
    end
end

for specificName,val in pairs(VehicleDistributions) do
    if not (string.find(specificName, "TruckBed") or
            string.find(specificName, "GloveBox") or
            string.find(specificName, "Seat") or
            string.find(specificName, "DriverSeat") or
            string.find(specificName, "Normal") or
            string.find(specificName, "Standard") or
            string.find(specificName, "Heavy") or
            string.find(specificName, "Sport")) and
        type(specificName) == "string"
    then
        --print(specificName)
        if val["specificId"] then
            --specificName = val["specificId"]
        else
            vehicles:add(parseVehicle(specificName, val))
        end
    end
end

return vehicles