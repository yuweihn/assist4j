if ARGV[1] == 'true' then
    if redis.call('get', KEYS[1]) == ARGV[2] then
        return redis.call('set', KEYS[1], ARGV[2], 'EX', ARGV[3], 'XX')
    else
        return redis.call('set', KEYS[1], ARGV[2], 'EX', ARGV[3], 'NX')
    end
else
    return redis.call('set', KEYS[1], ARGV[2], 'EX', ARGV[3], 'NX')
end
