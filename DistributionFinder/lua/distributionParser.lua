function stripExtras(line)
    return line
        :gsub("Base.", "")
        :gsub("TEXTURE_", "")
        :gsub("TINT","")
        :gsub("TEXTURE", "")
        :gsub("farming.", "")
end

function parseProcedural(container, vals, location)
    local ret = ""

    ret = ret .. "container[name:" .. stripExtras(container) .. "|procedural";
    if location then
        ret = ret .. "|location=" .. location
    end

    ret = ret .. "]\n"
    return ret;
end

function parseContainer(container, vals, location)
    if vals["procedural"] then
        return parseProcedural(container, vals, location)
    end

    local ret = ""

    if vals["rolls"] then
        ret = ret .. "container[name:" .. stripExtras(container) .. "|rolls:" .. vals["rolls"]
    end

    if (location) then
        ret = ret .. "|location:" .. location
    end

    ret = ret .. "]\n"

    if vals["items"] then
        for i,line in ipairs(vals["items"]) do
            if i%2 == 1 then
                ret = ret .. "item[name:" .. stripExtras(line)
            else
                ret = ret .. "|odds:" .. line .. "]\n"
            end
        end
    end

    if vals["junk"] then

    end

    return ret
end

function parseLocation(location, values)
    local ret = ""

    ret = ret .. "location[name:" .. stripExtras(location)
    if values["isShop"] then
        ret = ret .. "|isShop"
    end

    ret = ret .. "]\n"

    for container, vals in pairs(values) do
        if type(vals) ~= "table" then
            goto continueContainer
        end

        ret = ret .. parseContainer(container, vals, location)

        ::continueContainer::
    end

    return ret
end


local ret = ""
for k,v in pairs(distributionTable) do
    -- If it has an 'items' key, it is a container itself.
    if v["items"] then
        ret = ret .. parseContainer(k, v)
        goto continue
    end

    -- Otherwise, parse it as a location
    ret = ret .. parseLocation(k, v)

    ::continue::
end

return ret