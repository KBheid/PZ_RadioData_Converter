function stripExtras(line)
    return line
        :gsub("Base.", "")
        :gsub("TEXTURE_", "")
        :gsub("TINT","")
        :gsub("TEXTURE", "")
        :gsub("farming.", "")
end

function parseContainer(name, vals)
    itemList = luajava.newInstance("java.util.ArrayList")

    rolls = 1
    if vals["rolls"] then
        rolls = tonumber(vals["rolls"])
    end
    procedural = vals["procedural"] ~= nil

    lastItemName = ""
    for i,nameOrOdds in ipairs(vals["items"]) do
        if i%2 == 1 then
            lastItemName = stripExtras(nameOrOdds)
        else
            itemList:add(luajava.newInstance("LuaJavaDefines.Item", lastItemName, tonumber(nameOrOdds)))
        end
    end
    return luajava.newInstance("LuaJavaDefines.Container", name, rolls, itemList, procedural)
end

function parseLocation(name, vals)
    isShop = false
    if vals["isShop"] then
        isShop = true
    end

    containers = luajava.newInstance("java.util.ArrayList")
    for c,v in pairs(vals) do
        if type(v) == "table" then
            if v["items"] then
                containers:add(parseContainer(c,v))
            end
        end
    end

    return luajava.newInstance("LuaJavaDefines.Location", name, isShop, containers)
end

returnTable = {}
locationsList = luajava.newInstance("java.util.ArrayList")
containersList = luajava.newInstance("java.util.ArrayList")

for k,v in pairs(distributionTable) do
    -- If it has an 'items' key, it is a container itself.
    if v["items"] then
        if #v["items"] < 1 then
            goto continue
        end
        containersList:add(parseContainer(k, v))
        goto continue
    end

    -- Otherwise, parse it as a location
    locationsList:add(parseLocation(k,v))

    ::continue::
end

returnTable["locations"] = locationsList
returnTable["locationLesscontainers"] = containersList

return returnTable