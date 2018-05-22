package org.alexdev.kepler.messages.outgoing.catalogue;

import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.catalogue.CataloguePage;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;

import java.util.List;

public class CATALOGUE_PAGE extends MessageComposer {
    private final List<CatalogueItem> catalogueItems;
    private final CataloguePage page;

    public CATALOGUE_PAGE(CataloguePage cataloguePage, List<CatalogueItem> cataloguePageItems) {
        this.page = cataloguePage;
        this.catalogueItems = cataloguePageItems;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeKeyValue("i", this.page.getNameIndex());
        response.writeKeyValue("n", this.page.getName());
        response.writeKeyValue("l", this.page.getLayout());
        response.writeKeyValue("g", this.page.getImageHeadline());
        response.writeKeyValue("e", this.page.getImageTeasers());
        response.writeKeyValue("h", this.page.getBody());

        if (!StringUtil.isNullOrEmpty(this.page.getLabelPick())) {
            response.writeKeyValue("w", this.page.getLabelPick());
        }

        if (!StringUtil.isNullOrEmpty(this.page.getLabelExtra_s())) {
            response.writeKeyValue("s", this.page.getLabelExtra_s());
        }

        for (int labelDataId = 1; labelDataId < 11; labelDataId++) {
            String extraDataId = "label_extra_t_" + labelDataId;

            if (!this.page.getLabelExtra().containsKey(extraDataId)) {
                continue;
            }

            response.writeKeyValue("t" + labelDataId, this.page.getLabelExtra().get(extraDataId));
        }
    }

    @Override
    public short getHeader() {
        return 127; // "A"
    }
}