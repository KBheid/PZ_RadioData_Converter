function stripExtras(line)
    return line
        :gsub("Base.", "")
        :gsub("TEXTURE_", "")
        :gsub("TINT","")
        :gsub("TEXTURE", "")
end

function parseContainer(container, vals, location)
    local ret = ""

    if (location) then
        ret = ret .. (location .. ">")
    end

    if vals["rolls"] then
        ret = ret .. "container[" .. stripExtras(container) .. "|rolls:" .. vals["rolls"] .. "]\n"
    end

    if vals["items"] then
        for i,line in ipairs(vals["items"]) do
            if i%2 == 1 then
                ret = ret .. "item[" .. stripExtras(line)
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

    ret = ret .. "location[" .. stripExtras(location)
    if (values["isShop"] ~= nil) then
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


for k,v in pairs(distributionTable) do
    -- If it has an 'items' key, it is a container itself.
    if v["items"] ~= nil then
        io.write(parseContainer(k, v))
        goto continue
    end

    -- Otherwise, parse it as a location
    io.write(parseLocation(k, v))

    ::continue::
end
