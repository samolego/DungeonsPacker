package org.samo_lego.dungeons_packer.level.block;

public class EndMissionBlock extends AbstractLocalConvertableBlock {
    public static final String END = "end";

    public EndMissionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected String regionTags() {
        return END;
    }

    @Override
    protected String regionName() {
        return END;
    }
}
