res = nil
if ARGV[1] == 'true' then
    res = redis.call('get', KEYS[1])
    if res == ARGV[2] then
        local r = redis.call('set', KEYS[1], ARGV[2], 'EX', ARGV[3], 'XX')
        if (r ~= 'OK' and r ~= 'ok') then
            res = nil
        end
    else
        local r = redis.call('set', KEYS[1], ARGV[2], 'EX', ARGV[3], 'NX')
        if (r == 'OK' or r == 'ok') then
            res = ARGV[2]
        end
    end
else
    local r = redis.call('set', KEYS[1], ARGV[2], 'EX', ARGV[3], 'NX')
    if (r == 'OK' or r == 'ok') then
        res = ARGV[2]
    end
end

return res